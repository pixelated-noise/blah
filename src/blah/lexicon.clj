(ns blah.lexicon
  (:require [clojure.xml :as xml]
            [clojure.java.io :as io]
            [clojure.set :as set]
            [clojure.string :as str]))

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

(defn- substring [s start end]
  (when s
    (if (pos? end)
      (subs s start end)
      (subs s start (+ (.length s) end)))))

(defn- get-form [base suffix]
  (-> (cond
         ;; rule 1 - convert final "y" to "ie" if suffix does not start with "i"
		     ;; eg, cry + s = cries , not crys
         (and (str/ends-with? base "y") (not (str/starts-with? suffix "i")))
         (str (substring base 0 -1) "ie")

         ;; rule 2 - drop final "e" if suffix starts with "e" or "i"
		     ;; eg, like+ed = liked, not likeed
         (and (str/ends-with? base "e") (or (str/starts-with? suffix "e")
                                            (str/starts-with? suffix "i")))
         (substring base 0 -1)

         ;; rule 3 - insert "e" if suffix is "s" and base ends in s, x, z, ch, sh
		     ;; eg, watch+s -> watches, not watchs
         (and (str/starts-with? suffix "s") (or (str/ends-with? base "s")
                                                (str/ends-with? base "x")
                                                (str/ends-with? base "z")
                                                (str/ends-with? base "ch")
                                                (str/ends-with? base "sh")))
         (str base "e"))

       (str suffix)))

(defn- variant [w k suffix]
  (or (w k) (get-form (:base w) suffix)))

(defn- variants [{:keys [base lexical-category] :as w}]
  (case lexical-category
    :noun      [base
                (variant w :plural "s")]
    :adjective [base
                (variant w :comparative "er")
                (variant w :superlative "est")]
    :verb      [base
                (variant w :present3s "s")
                (variant w :past "ed")
                (variant w :past-participle "ed")
                (variant w :present-participle "ing")]
    [base]))

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

(defn get-by-id
  [lexicon id]
  (get-in lexicon [:by-id id]))

(defn get-by-base
  ([lexicon base]
   (get-by-base lexicon base nil))
  ([lexicon base category]
   (let [words (some->> (get-in lexicon [:by-base base])
                        (map #(assoc % :type :word)))]
     (if (and words category)
       (filter #(= category (:lexical-category %)) words)
       words))))

(defn get-by-variant ;;TODO almost identical to previous fn
  ([lexicon base]
   (get-by-variant lexicon base nil))
  ([lexicon base category]
   (let [words (some->> (get-in lexicon [:by-variant base])
                        (map #(assoc % :type :word)))]
     (if (and words category)
       (filter #(= category (:lexical-category %)) words)
       words))))

(defn create-simplenlg [data]
  (let [words   (->> data :content (map create-word))
        lexicon {:by-id      (zipmap (map :id words) words)
                 :by-base    (group-by :base words)
                 :by-variant (reduce (fn [index w]
                                       (merge-with concat index (zipmap (variants w) (repeat [w])))) {} words)}
        be      (get-by-base "be" :verb)]
    (update lexicon :by-variant merge (zipmap ["is" "am" "are" "was" "were"] (repeat [be])))))

(defn parse-simplenlg
  "Parses and loads a simplenlg lexicon from source s, which can be a File,
  InputStream or String naming a URI."
  [s]
  (-> (create-simplenlg (xml/parse s))
      (assoc :source s)))

(defmacro with [lex & body]
  `(binding [*current* lex]
     ~(cons 'do body)))

;; all possible keys (after rename)

(comment
  (def lex (parse-simplenlg (io/file "/Users/sideris/devel/third-party/simplenlg/src/main/resources/default-lexicon.xml")))

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
