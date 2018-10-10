(ns clj-atl-meetup-backend.routes.services
  (:require [ring.util.http-response :refer :all]
            [cheshire.core :as json]
            [clj-http.lite.client :as http]
            [clojure.string :as string]
            [compojure.api.sweet :refer :all]
            [schema.core :as s]))


(def show-db-debug-info (atom true))


(defmacro with-log
  "Catch and log exceptions."
  [& body]
  `(try
     ~@body
     (catch Exception e#
       (when @show-db-debug-info
         (println (str "---> exception " e#))
         (println (str "---> stack trace " (.printStackTrace e#)))
         (println (str "---> next exception " (.getNextException e#))))
       (throw e#))))

(def distance-api
  "Google Maps API.
See: https://developers.google.com/maps/documentation/distance-matrix/intro
#DistanceMatrixRequests"
  ;; Move key to env.
  (str "https://maps.googleapis.com/maps/api/distancematrix/json"
       "?key=AIzaSyAu8SYl9_90jSuvy6L47M5xkWvmnMsoMJo"))


(defn get-travel-distance [starting-address ending-address]
  (let [start (string/replace starting-address #" " "+")
        end (string/replace ending-address #" " "+")]
    (with-log
      (http/get (str distance-api "&origins=" start "&destinations=" end)))))


(defapi service-routes
  {:swagger {:ui "/swagger-ui"
             :spec "/swagger.json"
             :data {:info {:version "1.0.0"
                           :title "Sample API"
                           :description "Sample Services"}}}}

  (context "/api" []
    :tags ["thingie"]

    (GET "/plus" []
      :return       Long
      :query-params [x :- Long, {y :- Long 1}]
      :summary      "x+y with query-parameters. y defaults to 1."
      (ok (+ x y)))

    (POST "/minus" []
      :return      Long
      :body-params [x :- Long, y :- Long]
      :summary     "x-y with body-parameters."
      (ok (- x y)))

    (GET "/times/:x/:y" []
      :return      Long
      :path-params [x :- Long, y :- Long]
      :summary     "x*y with path-parameters"
      (ok (* x y)))

    (POST "/divide" []
      :return      Double
      :form-params [x :- Long, y :- Long]
      :summary     "x/y with form-parameters"
      (ok (/ x y)))

    (GET "/power" []
      :return      Long
      :header-params [x :- Long, y :- Long]
      :summary     "x^y with header-parameters"
      (ok (long (Math/pow x y))))))
