(ns blah.morphology
  (:require [clojure.string :as str]
            [clojure.core.match :refer [match]]
            [blah.lexicon :as lexicon]
            [blah.morphology.noun]
            [blah.morphology.verb]
            [blah.morphology.pronoun]
            [blah.morphology.adjective]))

(def noun blah.morphology.noun/noun)
(def verb blah.morphology.verb/verb)
(def pronoun blah.morphology.pronoun/pronoun)
(def adjective blah.morphology.adjective/adjective)

(defn do-morphology [{:keys [base category non-morph lex-entry]
                      :as   element}
                     lexicon]
  (if non-morph
    base
    (let [lex-entry (or lex-entry (lexicon/by-base base))]
      (case category
        :pronoun
        (pronoun element)
        :noun
        (noun element lex-entry)
        :verb
        (verb element lex-entry)
        :adjective
        (adjective element lex-entry)
        :adverb
        "TODO"
        base))))

;; (require '[blah.lexicon :as lex])
;; (def wolf (-> lex/lex (lex/by-base "wolf") first))
;; (noun {:number :plural} wolf)
;; (noun {:number :plural :possessive true} wolf)
;; (noun {:possessive true} wolf)
