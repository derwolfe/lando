(ns lando.web
  (:require [compojure.core :refer [defroutes]]
            [aleph.http :as http]
            [compojure.handler :refer [site]]
            [compojure.route :as route]
            [clojure.java.io :as io]
            [ring.util.response :as response]
            [ring.middleware.resource]
            [ring.middleware.content-type]
            [ring.middleware.not-modified]
            [ring.middleware.ssl]
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
      (ring.middleware.resource/wrap-resource "public")
      (ring.middleware.content-type/wrap-content-type)
      (ring.middleware.not-modified/wrap-not-modified)
      (wrap-dir-index)
      ;; (ring.middleware.ssl/wrap-hsts)
      ;; (wrap-force-tls)
      ))

(defn -main [& args]
  (http/start-server app {:port 8080}))
