(ns blah.spec
  (:require [clojure.spec.alpha :as s]))

;; ADVERTISEMENT
;; Most of these specs where inferred using spec-provider on a lexicon
;; https://github.com/stathissideris/spec-provider
;;
;; (def sp (sp/infer-specs (->> lex :by-id vals) ::lex-entry))
;; (sp/pprint-specs sp 'blah.lexicon 's)

(s/def ::base string?)
(s/def ::plural string?)

(s/def ::possessive boolean?)
(s/def ::number #{:singular :plural})
(s/def ::element
  (s/keys
   :opt-un
   [::base
    ::number
    ::person
    ::tense
    ::form
    ::default-inflection
    ::negated
    ::plural
    ::possessive]))

(s/def ::id string?)
(s/def ::category
  #{:determiner :verb :adverb :preposition :adjective :modal
    :conjunction :pronoun :noun})

(s/def ::predicative boolean?)
(s/def ::classifying boolean?)
(s/def ::colour boolean?)
(s/def ::non-count boolean?)
(s/def ::ditransitive boolean?)
(s/def ::intransitive boolean?)
(s/def ::proper boolean?)
(s/def ::present3s string?)
(s/def ::superlative string?)
(s/def ::intensifier boolean?)
(s/def ::comparative string?)
(s/def ::irregular boolean?)
(s/def ::past string?)
(s/def ::transitive boolean?)
(s/def ::qualitative boolean?)
(s/def ::verb-modifier boolean?)
(s/def ::present-participle string?)
(s/def ::past-participle string?)
(s/def ::sentence-modifier boolean?)

(s/def
 ::lex-entry
 (s/keys
  :req-un
  [::base ::category]
  :opt-un
  [::classifying
   ::colour
   ::comparative
   ::ditransitive
   ::id
   ::intensifier
   ::intransitive
   ::irregular
   ::non-count
   ::past
   ::past-participle
   ::plural
   ::predicative
   ::present-participle
   ::present3s
   ::proper
   ::qualitative
   ::sentence-modifier
   ::superlative
   ::transitive
   ::verb-modifier]))
