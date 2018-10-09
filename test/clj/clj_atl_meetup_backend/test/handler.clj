(ns clj-atl-meetup-backend.test.handler
  (:require [clojure.test :refer :all]
            [ring.mock.request :refer :all]
            [clj-atl-meetup-backend.handler :refer :all]
            [clj-atl-meetup-backend.middleware.formats :as formats]
            [muuntaja.core :as m]
            [mount.core :as mount]))

(defn parse-json [body]
  (m/decode formats/instance "application/json" body))

(use-fixtures
  :once
  (fn [f]
    (mount/start #'clj-atl-meetup-backend.config/env
                 #'clj-atl-meetup-backend.handler/app)
    (f)))

(deftest test-app
  (testing "main route"
    (let [response (app (request :get "/"))]
      (is (= 200 (:status response)))))

  (testing "not-found route"
    (let [response (app (request :get "/invalid"))]
      (is (= 404 (:status response))))))
