(ns garden-gnome.watcher
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [garden.core :as garden]
            [hawk.core :as hawk])
  (:import (java.io File)))


(defn compile! [{:keys [stylesheet compiler]}]
  (io/make-parents (:output-to compiler))
  (println (str "Garden Gnome: compiling #'" stylesheet))
  (garden/css compiler (var-get (find-var stylesheet))))


(defn- reload! [style-ns]
  (println "Garden Gnome: loading" style-ns)
  (require (symbol style-ns) :reload))


(defn- ns->file-name
  "Copied from clojure.tools.namespace.move because it's private there."
  [ns-sym]
  (str (-> (name ns-sym)
           (str/replace "-" "_")
           (str/replace "." File/separator))
       ".clj"))


(defn- ns-from-file? [style-ns ^File path]
  (let [styles-ns     (namespace style-ns)
        reloaded-path (.getPath path)
        ns-file-name  (ns->file-name styles-ns)]
    (.endsWith reloaded-path ns-file-name)))


(defn reload-and-compile! [configs]
  (doseq [[style-ns styles] (group-by #(-> % :stylesheet namespace)
                                      configs)]
    (reload! style-ns)
    (run! compile! styles)))


(defn- garden-reloader-handler [all-configs]
  (fn [_ctx {:keys [kind file] :as event}]
    (when (= kind :modify)
      (->> all-configs
           (filter #(ns-from-file? (:stylesheet %) file))
           (reload-and-compile!)))))


(defn start!
  "Starts a new watcher for the given configs.
  Use (default-config) to read from 'project-clj'"
  [configs]
  (let [source-paths (set (map :source-path configs))
        handler      (garden-reloader-handler configs)]
    (reload-and-compile! configs)
    (println "Garden: watching" (str/join ", " source-paths))
    (hawk/watch! [{:paths   source-paths
                   :handler handler}])))


(defn stop! [watch]
  (hawk/stop! watch)
  (println "Garden: stopped watching namespaces."))


(defn default-config []
  (if (.exists (io/file "garden.edn"))
    (edn/read-string (slurp "garden.edn"))
    (->> "project.clj"
         slurp
         edn/read-string
         (drop 3)
         (apply hash-map)
         :garden
         :builds)))
