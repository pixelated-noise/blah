(ns blah.morphology.noun
  (:require [blah.util :as util]
            [clojure.string :as str]))

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

(defn noun [{:keys [number proper possessive] :as element}
            lex-entry]
  (let [bf (util/base-form element lex-entry)
        r  (if (and (= :plural number) (not proper))
             (or (if (= :uncount (:default-inflection element))
                   bf
                   (:plural element))
                 (if (= :uncount (:default-inflection lex-entry))
                   bf
                   (:plural lex-entry))
                 (if (= :greco-latin-regular (:default-inflection element))
                   (greco-latin-plural-noun bf)
                   (regular-plural-noun bf)))
             bf)
        r  (if possessive
             (if (= \s (last r))
               (str r "'")
               (str r "'s"))
             r)]
    (merge element
           {:category    :canned-text
            :elided      false
            :realisation r})))
