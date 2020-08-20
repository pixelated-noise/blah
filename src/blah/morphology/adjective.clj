(ns blah.morphology.adjective
  (:require [blah.util :as util]
            [clojure.string :as str]))

(defn- double-comparative [base]
  (when base
    (str base (last base) "er")))

(defn- regular-comparative [base]
  (cond (re-matches #".*[b-z&&[^eiou]]y\b")
        (str/replace #"y\b" "ier")

        (str/ends-with? base "e")
        (str base "r")

        :else
        (str base "er")))

(defn- double-superlative [base]
  (when base
    (str base (last base) "est")))

(defn- regular-superlative [base]
  (cond (re-matches #".*[b-z&&[^eiou]]y\b")
        (str/replace #"y\b" "iest")

        (str/ends-with? base "e")
        (str base "st")

        :else
        (str base "est")))

(defn adjective [{:keys [default-inflection]
                  :as   element}
                 lex-entry]
  (let [bf (util/base-form element lex-entry)]
    (cond (:comparative element) ;;TODO SimpleNLG uses a boolean in MorphologyRules
          (or (:comparative element)
              (:comparative lex-entry)
              (if (= default-inflection :regular-double)
                (double-comparative bf)
                (regular-comparative bf)))

          (:superlative element) ;;TODO SimpleNLG uses a boolean in MorphologyRules
          (or (:superlative element)
              (:superlative lex-entry)
              (if (= default-inflection :regular-double)
                (double-superlative bf)
                (regular-superlative bf)))

          :else bf)))
