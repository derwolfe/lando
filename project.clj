(defproject lando "0.1.0-SNAPSHOT"
  :description "static file server"
  :url "https://github.com/derwolfe/lando"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [http-kit "2.1.19"]
                 [ring "1.4.0"]
                 [compojure "1.4.0"]
                 [ring/ring-defaults "0.1.5"]
                 [environ "1.0.1"]]
  :plugins [[lein-environ "1.0.1"]]
  :min-lein-version "2.0.0"
  :uberjar-name "lando.jar"
  :target-path "target/%s"
  :main "lando.web"
  :profiles {:production {:env {:production true}}})
