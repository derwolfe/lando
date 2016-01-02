(ns lando.web
  (:use [org.httpkit.server :only [run-server]])
  (:require [compojure.core :refer [defroutes]]
            [compojure.handler :refer [site]]
            [compojure.route :as route]
            [clojure.java.io :as io]
            [ring.util.response :as response]
            [ring.middleware.reload :as reload]
            [ring.middleware.resource :as rmr]
            [ring.middleware.content-type :as rmct]
            [ring.middleware.not-modified :as rmnm]
            [ring.middleware.ssl :as rms]
            [environ.core :refer [env]]))

(defn wrap-dir-index
  "Middleware that allows the server to serve index.html pages
  without displaying the `/index.html` at the end of the address."
  [handler]
  (fn [req]
    (handler
     (update-in req [:uri]
                (fn [uri]
                  (if (.endsWith uri "/")
                    (str uri "/index.html")
                    uri))))))

(defn wrap-force-tls
  "Almost like in lib-noir.
   If the request's scheme is not https [and is for 'secure.'], redirect with https.
   Also checks the X-Forwarded-Proto header."
  [app]
  (fn [req]
    (let [headers (:headers req)
          host    (headers "host")
          scheme (:sheme req)
          lb-header (headers "x-forwarded-proto")]
      (if (or (= :https scheme)
              (= "https" lb-header))
        (app req)
        (response/redirect (str "https://" host (:uri req)) :permanent)))))

(defroutes all-routes
  (route/not-found (slurp (io/resource "error/404.html"))))

(def app
  (-> (site all-routes)
      (rmr/wrap-resource "public")
      (rmct/wrap-content-type)
      (rmnm/wrap-not-modified)
      (wrap-dir-index)
      (rms/wrap-hsts)
      (wrap-force-tls)))

(defn -main [& args]
  (run-server app {:port 8080}))
