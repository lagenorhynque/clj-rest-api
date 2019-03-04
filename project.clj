(defproject clj-rest-api "0.1.0-SNAPSHOT"
  :description "Clojure REST API example"
  :url "https://github.com/lagenorhynque/clj-rest-api"
  :min-lein-version "2.0.0"
  :dependencies [[camel-snake-kebab "0.4.0"]
                 [clojure.java-time "0.3.2"]
                 [duct.module.pedestal "2.0.1"]
                 [duct/core "0.7.0"]
                 [duct/module.logging "0.4.0"]
                 [duct/module.sql "0.5.0"]
                 [funcool/struct "1.3.0"]
                 [honeysql "0.9.4"]
                 [metosin/ring-http-response "0.9.1"]
                 [org.clojure/clojure "1.10.0"]
                 [org.mariadb.jdbc/mariadb-java-client "2.4.0"]]
  :plugins [[duct/lein-duct "0.11.2"]]
  :main ^:skip-aot clj-rest-api.main
  :resource-paths ["resources" "target/resources"]
  :prep-tasks     ["javac" "compile" ["run" ":duct/compiler"]]
  :middleware     [lein-duct.plugin/middleware]
  :profiles
  {:repl {:prep-tasks   ^:replace ["javac" "compile"]
          :repl-options {:init-ns user}}
   :dev  [:shared :project/dev :profiles/dev]
   :test [:shared :project/dev :project/test :profiles/test]
   :uberjar [:shared :project/uberjar]

   :shared {}
   :project/dev  {:source-paths   ["dev/src"]
                  :resource-paths ["dev/resources"]
                  :dependencies   [[clj-http "3.9.1"]
                                   [eftest "0.5.7"]
                                   [integrant/repl "0.3.1"]
                                   [orchestra "2019.02.06-1"]
                                   [pjstadig/humane-test-output "0.9.0"]]
                  :injections [(require 'pjstadig.humane-test-output)
                               (pjstadig.humane-test-output/activate!)]}
   :project/test {}
   :project/uberjar {:aot :all
                     :uberjar-name "clj-rest-api.jar"}
   :profiles/dev {}
   :profiles/test {}})
