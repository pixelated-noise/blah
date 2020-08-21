(ns blah.morphology.adverb
  (:require [blah.morphology.adjective :as adjective]
            [blah.util :as util]))

(defn adverb [{:keys [comparative superlative]
               :as   element} lex-entry]
  (let [bf (util/base-form element lex-entry)]
    (cond comparative ;;TODO SimpleNLG uses a boolean in MorphologyRules
          (or (:comparative element)
              (:comparative lex-entry)
              (adjective/regular-comparative bf))

          superlative ;;TODO SimpleNLG uses a boolean in MorphologyRules
          (or (:superlative element)
              (:superlative lex-entry)
              (adjective/regular-superlative bf))

          :else
          bf)))
