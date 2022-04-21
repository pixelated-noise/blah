(ns blah.lexicon
  (:require [clojure.xml :as xml]
            [clojure.java.io :as io]
            [clojure.set :as set]))

(def ^:dynamic *current*)

;; see simplenlg.lexicon.XMLLexicon

(def key-mapping
  {:irreg             :irregular

   :nonCount          :uncountable
   :uncount           :uncountable
   :groupuncount      :uncountable

   :pastParticiple    :past-participle
   :presentParticiple :present-participle
   :sentence_modifier :sentence-modifier
   :verbModifier      :verb-modifier
   :verb_modifier     :verb-modifier
   :category          :lexical-category

   ;; not actually observed in simplenlg lexicon
   :regd              :regular-double
   :glreg             :greco-latin-regular
   :inv               :invariant})

(defn create-word [e]
  (let [c (-> e :content)
        w (-> (zipmap (map :tag c)
                      (map (fn [x] (-> x :content first (or true))) c))
              (set/rename-keys key-mapping)
              (update :lexical-category keyword))]
    (if ((some-fn :irregular
                  :regular-double
                  :greco-latin-regular
                  :uncountable
                  :invariant) w)
      w
      (assoc w :regular true))))

(defn create-simplenlg [data]
  (let [words (->> data :content (map create-word))]
    ;;TODO addSpecialCases() -- adds variants of "be"
    {:by-id   (zipmap (map :id words) words)
     :by-base (group-by :base words)
     ;;:by-variant () ;;TODO
     }))

(defn parse-simplenlg
  "Parses and loads a simplenlg lexicon from source s, which can be a File,
  InputStream or String naming a URI."
  [s]
  (-> (create-simplenlg (xml/parse s))
      (assoc :source s)))

(defn get-by-base
  ([lexicon base]
   (get-by-base lexicon base nil))
  ([lexicon base category]
   (let [words (->> lexicon
                    (get-in lexicon [:by-base base])
                    (map #(assoc % :type :word)))]
     (if-not category
       words
       (filter #(= category (:lexical-category %)) words)))))

;;(def lex (parse-simplenlg (io/file "/Users/sideris/devel/third-party/simplenlg/src/main/resources/default-lexicon.xml")))

(defmacro with [lex & body]
  `(binding [*current* lex]
     ~(cons 'do body)))

;; all possible keys (after rename)

(comment
  (->> (io/file "/Users/sideris/devel/third-party/simplenlg/src/main/resources/default-lexicon.xml")
       xml/parse
       :content
       (map create-word)
       (mapcat keys)
       distinct sort)

  (:base
   :classifying
   :colour
   :comparative
   :ditransitive
   :id
   :intensifier
   :intransitive
   :irregular
   :lexical-category
   :non-count
   :past
   :past-participle
   :plural
   :predicative
   :present-participle
   :present3s
   :proper
   :qualitative
   :sentence-modifier
   :superlative
   :transitive
   :verb-modifier))
