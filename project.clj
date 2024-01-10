(defproject clj-rest-api "0.1.0-SNAPSHOT"
  :description "Clojure REST API example"
  :url "https://github.com/lagenorhynque/clj-rest-api"
  :min-lein-version "2.0.0"
  :dependencies [[camel-snake-kebab "0.4.3"]
                 [clojure.java-time "1.4.2"]
                 [com.github.seancorfield/honeysql "2.5.1103"]
                 [com.github.seancorfield/next.jdbc "1.3.909"]
                 [duct.module.cambium "1.3.1" :exclusions [cheshire]]
                 [duct.module.pedestal "2.2.0"]
                 [duct/core "0.8.0" :exclusions [medley]]
                 [duct/module.sql "0.6.1"]
                 [funcool/struct "1.4.0" :exclusions [org.clojure/clojurescript]]
                 [integrant "0.8.0"]
                 [metosin/ring-http-response "0.9.3" :exclusions [ring/ring-core]]
                 [org.clojure/clojure "1.11.1"]
                 [org.mariadb.jdbc/mariadb-java-client "3.3.2"]
                 [org.slf4j/slf4j-api "2.0.11"]]
  :plugins [[duct/lein-duct "0.12.3"]]
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
                  :dependencies   [[clj-http "3.12.3" :exclusions [commons-io]]
                                   [com.bhauman/rebel-readline "0.1.4"]
                                   [com.gearswithingears/shrubbery "0.4.1"]
                                   [eftest "0.6.0" :exclusions [org.clojure/tools.logging
                                                                org.clojure/tools.namespace]]
                                   [fipp "0.6.26"]
                                   [hawk "0.2.11"]
                                   [integrant/repl "0.3.3" :exclusions [integrant]]
                                   [orchestra "2021.01.01-1"]
                                   [pjstadig/humane-test-output "0.11.0"]]
                  :plugins [[jonase/eastwood "1.4.2"]
                            [lein-ancient "0.7.0"]
                            [lein-cloverage "1.2.4"]
                            [lein-codox "0.10.8"]
                            [lein-kibit "0.1.8"]]
                  :aliases {"rebel" ^{:doc "Run REPL with rebel-readline."}
                            ["trampoline" "run" "-m" "rebel-readline.main"]
                            "test-coverage" ^{:doc "Execute cloverage."}
                            ["cloverage" "--ns-exclude-regex" "^(:?dev|user)$" "--codecov" "--junit"]
                            "lint" ^{:doc "Execute eastwood and kibit."}
                            ["do"
                             ["eastwood" "{:config-files [\"dev/resources/eastwood_config.clj\"]
                                           :source-paths [\"src\"]
                                           :test-paths []}"]
                             ["kibit"]]}
                  :injections [(require 'pjstadig.humane-test-output)
                               (pjstadig.humane-test-output/activate!)]
                  :codox {:output-path "target/codox"
                          :source-uri "https://github.com/lagenorhynque/clj-rest-api/blob/master/{filepath}#L{line}"
                          :metadata {:doc/format :markdown}}}
   :project/test {}
   :project/uberjar {:aot :all
                     :uberjar-name "clj-rest-api.jar"}
   :profiles/dev {}
   :profiles/test {}})
