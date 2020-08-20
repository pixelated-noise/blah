(ns blah.morphology.pronoun
  (:require [clojure.core.match :refer [match]]))

(def pronouns
  {:singular {:i      ["I" "you" "he" "she" "it"]
	            :me     ["me" "you" "him" "her" "it"]
	            :myself ["myself" "yourself" "himself" "herself" "itself"]
	            :mine   ["mine" "yours" "his" "hers" "its"]
	            :my     ["my" "your" "his" "her" "its"]}
	 :plural   {:i      ["we" "you" "they" "they" "they"]
	            :me     ["us" "you" "them" "them" "them"]
	            :myself ["ourselves" "yourselves" "themselves" "themselves" "themselves"]
	            :mine   ["ours" "yours" "theirs" "theirs" "theirs"]
	            :my     ["our" "your" "their" "their" "their"]}})

(def wh-pronouns #{"who" "what" "which" "where" "why" "how" "how many"})

(defn pronoun [{:keys [base non-morph gender person discourse-function
                       number reflexive possessive passive]
                :or   {passive    false
                       reflexive  false
                       possessive false}
                :as   element}]
  (if (or non-morph (wh-pronouns base))
    base
    (let [position (match
                     [discourse-function passive reflexive possessive]
                     [_                  _       true      _         ] :myself
                     [:specifier         _       _         true      ] :my
                     [_                  _       _         true      ] :mine
                     [:subject           false   _         _         ] :i
                     [:object            true    _         _         ] :i
                     [:specifier         _       _         _         ] :i
                     [:complement        true    _         _         ] :i
                     :else                                             :me)
          idx      (match
                     [person  gender    ]
                     [:first  _         ] 0
                     [:second _         ] 1
                     [:third  :masculine] 2
                     [:third  :feminine ] 3
                     [:third  :neuter   ] 4)]
      (get-in pronouns [number position idx]))))
