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

(defmulti realize (fn [{:keys [type]}] (get type-mapping type type)))

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

(defmethod realize :noun-phrase
  [{:keys [children]}]
  )

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

(defn realize [syntax-processor phrase]
  (-> phrase
      (realize-list (pre-modifiers phrase) syntax-processor :pre-modifier)
      (realize-head syntax-processor)
      (realize-complements syntax-processor)
      (realize-list (post-modifiers phrase) syntax-processor :post-modifier)))

;; NLGElement
{:category    ""
 :features    {:number :plural
               :tense  :present}
 :parent      nil
 :realization ""
 :factory     nil
 :children    []}

;; Hierarchy

;; NLGElement
;;   PhraseElement
;;     PPPhraseSpec
;;     SPhraseSpec
;;     NPPhraseSpec
;;     AdjPhraseSpec
;;     AdvPhraseSpec
;;     VPPhraseSpec
;;   DocumentElement
;;   StringElement
;;   ListElement
;;   InflectedWordElement
;;   CoordinatedPhraseElement
;;   WordElement

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
