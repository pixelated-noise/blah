(ns blah.morphology
  (:require [blah.word :as word]
            [clojure.string :as str]))

(defn- base-form [{:keys [category base] :as element} word]
  (if (= :verb category)
    (or (word/default-spelling-variant word) base)
    ))

(defn verb [{:keys [number person tense form default-inflection negated]
             :or   {tense :present}
             :as   element}
            word]
  (let [bf (base-form element word)]
    (cond (and negated (= :bare-infinitive form))
          bf

          (= :present-participle form)
          (or (:present-participle element)
              (:present-participle word)
              (if (= default-inflection :regular-double)
                (double-pres-part-verb bf)
                (regular-pres-part-verb bf)))

          (and (= :past tense) (= :past-participle form)
               )))
  )

(defn- regular-plural-noun
  "Builds a plural for regular nouns. The rules are performed in this order:

  * For nouns ending -Cy, where C is any consonant, the ending becomes -ies. For
    example, fly becomes flies.

  * For nouns ending -ch, -s, -sh, -x or -z the ending becomes -es. For example,
    box becomes boxes.

  * All other nouns have -s appended the other end. For example, dog becomes
    dogs."
  [base]
  (when base
    (cond (re-matches #".*[b-z&&[^eiou]]y\b" base)
          (str/replace base #"y\b" "ies")

          ;;TODO "o" (but! radio patio)
          ;;TODO "fe" -> "ves" (not roof, not giraffe)

          (re-matches #".*([szx]|[cs]h)\b" base)
          (str base "es")

          :else
          (str base "s"))))

(defn- greco-latin-plural-noun
  "Builds a plural for Greco-Latin regular nouns."
  [base]
  (when base
    (cond (str/ends-with? base "us")
          (str/replace base "us\b" "i")

          (str/ends-with? base "ma")
          (str base "ta")

          (re-matches #".*[(um)(on)]\b")
          (str/replace base #".*[(um)(on)]\b" "a")

          (str/ends-with? base "sis")
          (str/replace base #"sis\b" "ses")

          (str/ends-with? base "is")
          (str/replace base #"is\b" "ides")

          (str/ends-with? base "men")
          (str/replace base #"men\b" "mina")

          (str/ends-with? base "ex")
          (str/replace base #"ex\b" "ices")

          (str/ends-with? base "x")
          (str/replace base #"x\b" "ces")

          :else
          base)))

(defn noun [{:keys [number proper possesive] :as element}
            word]
  (let [bf (base-form element word)
        r  (if (and (= :plural number) proper)
             (or (if (= :uncount (:default-inflection element))
                   bf
                   (:plural element))
                 (if (= :uncount (:default-inflection word))
                   bf
                   (:plural word))
                 (if (= :greco-latin-regular (:default-inflection element))
                   (greco-latin-plural-noun bf)
                   (regular-plural-noun bf)))
             bf)
        r  (if possessive
             (if (= \s (last r))
               (str r "'")
               (str r "'s"))
             s)]
    (merge element
           {:category    :canned-text
            :elided      false
            :realisation r})))
