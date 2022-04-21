(ns blah.lexicon
  (:require [clojure.xml :as xml]
            [clojure.java.io :as io]
            [clojure.set :as set]))

;; see simplenlg.lexicon.XMLLexicon

(def key-mapping
  {:irreg             :irregular
   :nonCount          :non-count
   :pastParticiple    :past-participle
   :presentParticiple :present-participle
   :sentence_modifier :sentence-modifier
   :verbModifier      :verb-modifier
   :verb_modifier     :verb-modifier})

(defn create-word [e]
  (let [c (-> e :content)]
    (-> (zipmap (map :tag c)
                (map (fn [x] (-> x :content first (or true))) c))
        (set/rename-keys key-mapping)
        (update :category keyword))))

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

(defn lookup-by-base [lexicon base]
  (get-in lexicon [:by-base base]))

;;(def lex (parse-simplenlg (io/file "/Users/sideris/devel/third-party/simplenlg/src/main/resources/default-lexicon.xml")))
