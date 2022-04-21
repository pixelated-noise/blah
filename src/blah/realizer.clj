(ns blah.realizer)

(def type-mapping
  {:cp     :coordinated-phrase
   :adv-p  :adverb-phrase
   :adj-p  :adjective-phrase
   :pp     :prepositional-phrase
   :vp     :verb-phrase
   :np     :noun-phrase
   :cl     :clause-phrase
   :clause :clause-phrase
   :w      :word
   :iw     :inflected-word})

(defmulti realize (fn [{:keys [type] :as element}]
                    (cond (vector? element) :list
                          (string? element) :string
                          :else (get type-mapping type type))))

;; syntax.realize
;; morphology.realize
;; orthography.realize
;; formatter.realize

(defmethod realize :coordinated-phrase
  [{:keys [children]}]
  )

(defmethod realize :word
  [{:keys [children]}]
  )

(defmethod realize :inflected-word
  [{:keys [children]}]
  )

(defmethod realize :adverb-phrase
  [{:keys [children]}]
  )

(defmethod realize :adjective-phrase
  [{:keys [children]}]
  )

(defmethod realize :prepositional-phrase
  [{:keys [children]}]
  )

(defmethod realize :verb-phrase
  [{:keys [children]}]
  )

(defn make-pronoun [{:keys [person gender number possessive discourse-function]
                     :as   phrase}]
  (let [pronoun (cond
                  (= person :first)     "I"
                  (= person :second)    "you"
                  (= gender :feminine)  "she"
                  (= gender :masculine) "he"
                  :else                 "it")]
    ;; TODO this is actually looked up in the lexicon
    {:base-form          pronoun
     :lexical-category   :pronoun
     :discourse-function (or discourse-function :specifier)
     :person             person
     :gender             gender
     :possessive         possessive
     :number             number}))

(def positions {:qualitative 1
                :colour      2
                :classifying 3
                :noun        4})

(defn sort-np-pre-modifiers [modifiers]
  ;;TODO - implemented in NounPhraseHelper
  modifiers)

(defmethod realize :noun-phrase
  [{:keys [head children elided pronomial specifier raised adjective-ordering
           pre-modifiers complements post-modifier]
    :as   phrase}]
  (when (and phrase (not elided))
    (if pronomial
      (make-pronoun phrase)
      {:type     :list
       :children [(-> (realize specifier) (assoc :discourse-function :specifier))
                  ;; pre-modifiers
                  (if adjective-ordering
                    (realize (sort-np-pre-modifiers pre-modifiers))
                    (realize pre-modifiers))
                  ;; head noun
                  (merge
                   (select-keys phrase [:elided :gender :acronym :number :person :possessive :passive])
                   {:discourse-function :subject}
                   (realize head))
                  ;; complements
                  (merge
                   {:discourse-function :complement}
                   (realize complements))
                  ;; post-modifier
                  (merge
                   {:discourse-function :complement}
                   (realize post-modifier))]})))

(defmethod realize :clause-phrase
  [{:keys [children]}]
  )

(defmethod realize :list
  [{:keys [children]}]
  [:list (map realize children)])

(defmethod realize :document
  [{:keys [children]}]
  (map realize children))


(declare realize-list)
(declare pre-modifiers)
(declare post-modifiers)
(declare realize-complements)
(declare realize-head)

(defn realize [phrase]
  (-> phrase
      (realize-list (pre-modifiers phrase) :pre-modifier)
      (realize-head)
      (realize-complements)
      (realize-list (post-modifiers phrase) :post-modifier)))

;; NLGElement
{:category    ""
 :features    {:number :plural
               :tense  :present}
 :parent      nil
 :realization ""
 :factory     nil
 :children    []}

;;;;;;;;;;;;;;;;;;;;;
;; ElementCategory
;;   DocumentCategory: Document, Section, Paragraph, Sentence, EnumeratedList, ListItem
;;   PhraseCategory: Clause, AdjectivePhrase, AdverbPhrase, NounPhrase, PrepositionalPhrase, VerbPhrase, CannedText
;;   LexicalCategory: Any, Symbol, Noun, Adjective, Adverb, Verb, Determiner, Pronoun, Conjunction, Preposition, Complementiser, Modal, Auxiliary


;; DocumentCategory.hasSubPart has some interesting info on how you can
;; structure a document. One possibility is:

[:document
 [:section
  [:paragraph
   [:sentence
    [:phrase]]]]]

;; SyntaxProcessor: This is the processor for handling syntax within the
;; SimpleNLG. The processor translates phrases into lists of words.
