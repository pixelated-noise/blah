(ns blah.morphology
  (:require [clojure.string :as str]
            [clojure.core.match :refer [match]]
            [blah.morphology.noun]
            [blah.morphology.verb]
            [blah.morphology.pronoun]))

(def noun blah.morphology.noun/noun)
(def verb blah.morphology.verb/verb)
(def pronoun blah.morphology.pronoun/pronoun)

;; (require '[blah.lexicon :as lex])
;; (def wolf (-> lex/lex (lex/by-base "wolf") first))
;; (noun {:number :plural} wolf)
;; (noun {:number :plural :possessive true} wolf)
;; (noun {:possessive true} wolf)
