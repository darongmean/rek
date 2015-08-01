(set-env!
  :source-paths #{"src/main/cljs" "src/main/clojure" "src/test/cljs"}
  :resource-paths #{"html" "resources"}
  :dependencies '[[adzerk/boot-cljs "0.0-2814-4" :scope "test"]
                  [adzerk/boot-cljs-repl "0.1.9" :scope "test"]
                  [adzerk/boot-reload "0.2.4" :scope "test"]
                  [pandeiro/boot-http "0.6.1" :scope "test"]
                  [infracanophile/boot-cljs-test "0.3.1-SNAPSHOT" :scope "test"]
                  [org.clojure/clojure "1.6.0"]
                  [reagent "0.5.0"]
                  [org.clojure/test.check "0.7.0"]])

(require
  '[adzerk.boot-cljs :refer [cljs]]
  '[adzerk.boot-cljs-repl :refer [cljs-repl start-repl]]
  '[adzerk.boot-reload :refer [reload]]
  '[pandeiro.boot-http :refer [serve]]
  '[infracanophile.boot-cljs-test :refer [cljs-test-runner run-cljs-test]])

(deftask build []
  (comp
    (speak)
    (cljs)))

(deftask autotest []
  (comp
    (watch)
    (speak)
    (cljs-test-runner :namespaces '[test.rek.board])
    (cljs)
    (run-cljs-test :cmd "phantomjs")))

(deftask run []
  (comp (serve)
    (watch)
    (cljs-repl)
    (reload)
    (build)))

(deftask production []
  (task-options! cljs {:optimizations :advanced
                       ;; pseudo-names true is currently required
                       ;; https://github.com/martinklepsch/pseudo-names-error
                       ;; hopefully fixed soon
                       :pseudo-names  true})
  identity)

(deftask development []
  (task-options! cljs {:optimizations :none
                       :unified-mode  true
                       :source-map    true}
    reload {:on-jsload 'darong.rek.app/init})
  identity)

(deftask dev
  "Simple alias to run application in development mode"
  []
  (comp
    (development)
    (run)))

(defn- generate-lein-project-file!
  [& {:keys [keep-project] :or {:keep-project true}}]
  (let [pfile (clojure.java.io/file "project.clj")
        pname (or (get-env :project) 'boot-project)
        pvers (or (get-env :version) "0.1.0-SNAPSHOT")
        prop  #(when-let [x (get-env %2)] [%1 x])
        head  (list* 'defproject pname pvers
                (concat
                  (prop :url :url)
                  (prop :license :license)
                  (prop :description :description)
                  [:dependencies (get-env :dependencies)
                   :source-paths (vec (get-env :source-paths))
                   :test-paths (vec (get-env :test-paths))]))
        proj  (pp-str (concat head (mapcat identity (get-env :lein))))]
    (if-not keep-project (.deleteOnExit pfile))
    (spit pfile proj)))

(deftask gen-project-clj
  "Generate a leiningen `project.clj` file.
  This task generates a leiningen `project.clj` file based on the boot
  environment configuration, including project name and version (generated
  if not present), dependencies, and source paths. Additional keys may be added
  to the generated `project.clj` file by specifying a `:lein` key in the boot
  environment whose value is a map of keys-value pairs to add to `project.clj`."
  [] (generate-lein-project-file! :keep-project true))
