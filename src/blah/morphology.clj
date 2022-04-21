(ns blah.morphology
  (:require [clojure.string :as str]
            [clojure.core.match :refer [match]]
            [blah.lexicon :as lexicon]
            [blah.morphology.noun]
            [blah.morphology.verb]
            [blah.morphology.pronoun]
            [blah.morphology.adjective]
            [blah.morphology.adverb]))

(def noun blah.morphology.noun/noun)
(def verb blah.morphology.verb/verb)
(def pronoun blah.morphology.pronoun/pronoun)
(def adjective blah.morphology.adjective/adjective)
(def adverb blah.morphology.adverb/adverb)

(defn do-morphology [{:keys [base category non-morph lex-entry]
                      :as   element}
                     lexicon]
  (if non-morph
    base
    (let [lex-entry (or lex-entry (first (lexicon/get-by-base base category)))]
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
        (adverb element lex-entry)
        base))))

(defn realize-element [element])

(defn realize [elements]
  (->> elements
       (map realize-element)
       (remove nil?)))

;; (require '[blah.lexicon :as lex])
;; (def wolf (-> lex/lex (lex/by-base "wolf") first))
;; (noun {:number :plural} wolf)
;; (noun {:number :plural :possessive true} wolf)
;; (noun {:possessive true} wolf)

:inflected-word
"string"
:word
:document
:list
:coordinated-phrase
