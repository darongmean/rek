(defproject
  boot-project
  "0.1.0-SNAPSHOT"
  :dependencies
  [[adzerk/boot-cljs "0.0-2814-4" :scope "test"]
   [adzerk/boot-cljs-repl "0.1.9" :scope "test"]
   [adzerk/boot-reload "0.2.4" :scope "test"]
   [pandeiro/boot-http "0.6.1" :scope "test"]
   [reagent "0.5.0"]]
  :source-paths
  ["src/clojure" "src/cljs"])