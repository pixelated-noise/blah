(ns blah.util)

(defn base-form [{:keys [category base] :as element} lex-entry]
  (if (= :verb category)
    (or (:default-spell lex-entry) base)
    (or base (:default-spell lex-entry))))
