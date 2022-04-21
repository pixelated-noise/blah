(ns dev
  (:require [cognitect.transcriptor :as xr]))

(defn xr-run []
  (doseq [repl-file (xr/repl-files "./xr")]
    (xr/run repl-file)))
