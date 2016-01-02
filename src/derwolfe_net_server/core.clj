(ns derwolfe-net-server.core
  (:require [compojure.core :refer [defroutes ANY]]
            [compojure.handler :refer [site]]
            [compojure.route :as route]
            [clojure.java.io :as io]
            [environ.core :refer [env]]))

(defroutes app
  (route/resources "/")
  (ANY "*" [] (route/not-found (slurp (io/resource "404.html")))))
