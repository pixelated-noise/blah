(ns blah.morphology
  (:require [blah.word :as word]
            [clojure.string :as str]))

(defn- base-form [{:keys [category base] :as element} lex-entry]
  (if (= :verb category)
    (or (:default-spell lex-entry) base)
    (or base (:default-spell lex-entry))))

(defn- be? [base] (= "be" (str/lower-case base)))

(defn- double-pres-part-verb
  "Builds the present participle form for verbs that follow the doubling form of
  the last consonant. `-ing` is added to the end after the last consonant is
  doubled. For example, `tug` becomes `tugging`."
  [base]
  (when base
    (str base (last base) "ing")))

(defn- regular-pres-part-verb
  "Builds the present participle form for regular verbs. The rules are performed
  in this order:

  * If the verb is `be` then the realised form is `being`.

  * For verbs ending `-ie` the ending becomes `-ying`. For example, `tie`
    becomes `tying`.

	* For other verbs ending in `-e` (but not `-ie`, `ye`, `-ee` nor `-oe`) the
    ending becomes `-ing`. For example, `chase` becomes `chasing`.

	* For verbs ending `-ee`, `-oe` or `-ye` then `-ing` is added to the end. For
    example, `canoe` becomes `canoeing`.

	* For all other verbs, `-ing` is added to the end. For example, `dry` becomes
    `drying`."
  [base]
  (when base
    (cond (be? base)
          "being"

          (str/ends-with? base "ie")
          (str/replace base #"ie\b" "ying")

          (re-matches #".*[^iyeo]e\b" base)
          (str/replace base #"e\b" "ing")

          :else
          (str base "ing"))))

(defn- double-past-verb
  "Builds the past-tense form for verbs that follow the doubling form of the last
  consonant. `-ed` is added to the end after the last consonant is doubled. For
  example, `tug` becomes `tugged`."
  [base]
  (when base
    (str base (last base) "ed")))

(defn- regular-past-verb
  "Builds the past-tense form for regular verbs. The rules are performed in this
  order:

	* If the verb is `be` and the number agreement is plural then the realised
    form is `were`.

	* If the verb is `be` and the number agreement is singular then the realised
    form is `was`, unless the person is second, in which case it's `were`.

	* For verbs ending `-e` the ending becomes `-ed`. For example, `chased`
    becomes `chased`.

  * For verbs ending `-Cy`, where C is any consonant, the ending becomes
    `-ied`. For example, `dry` becomes `dried`.

	* For every other verb, `-ed` is added to the end of the word."
  [base number person]
  (cond (be? base)
        (if (or (= number :plural) (= person :second))
          "were"
          "was")

        (str/ends-with? base "e")
        (str base "d")

        (re-matches #".*[b-z&&[^eiou]]y\b" base)
        (str/replace base #"y\b" "ied")

        :else
        (str base "ed")))

(defn- present-3s-verb
  "Builds the third-person singular form for regular verbs. The rules are
  performed in this order:

	* If the verb is `be` the realised form is `is`.

	* For verbs ending `-ch`, `-s`, `-sh`, `-x` or `-z` the ending becomes
    `-es`. For example, `preach` becomes `preaches`.

	* For verbs ending `-y` the ending becomes `-ies`. For
	  example, `fly` becomes `flies`.

	* For every other verb, `-s` is added to the end of the word."
  [base]
  (when base
    (cond (be? base)
          "is"

          (re-matches #".*[szx(ch)(sh)]\b" base)
          (str base "es")

          (re-matches ".*[b-z&&[^eiou]]y\b" base)
          (str/replace #"y\b" "ies")

          :else
          (str base "s"))))

(defn verb [{:keys [number person tense form default-inflection negated
                    discourse-function]
             :or   {tense  :present
                    number :singular}
             :as   element}
            lex-entry]
  (let [bf (base-form element lex-entry)]
    (cond (and negated (= :bare-infinitive form))
          bf

          (= :present-participle form)
          (or (:present-participle element)
              (:present-participle lex-entry)
              (if (= default-inflection :regular-double)
                (double-pres-part-verb bf)
                (regular-pres-part-verb bf)))

          (or (= :past tense) (= :past-participle form))
          (if (= :past-participle form)
            (or (:past-participle element)
                (:past-participle lex-entry)
                (when (be? bf) "been")
                (if (= default-inflection :regular-double)
                  (double-past-verb bf)
                  (regular-past-verb bf number person)))
            (or (:past element)
                (:past lex-entry)
                (if (= default-inflection :regular-double)
                  (double-past-verb bf)
                  (regular-past-verb bf number person))))

          (and (= number :singular)
               (= tense :present)
               (or (not person) (= person :third)))
          (or (:present3s element)
              (and (not (be? bf)) (:present3s element))
              (present-3s-verb))

          :else
          (if (be? bf)
            (if (and (= person :first) (= number :singular)) "am" "are")
            bf))))

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
  (let [bf (base-form element lex-entry)
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

;; (require '[blah.lexicon :as lex])
;; (def wolf (-> lex/lex (lex/by-base "wolf") first))
;; (noun {:number :plural} wolf)
;; (noun {:number :plural :possessive true} wolf)
;; (noun {:possessive true} wolf)
