(ns lcmap.see.backend.native.models.sample
  "This sample runner demonstrates kicking off a job that is executed on the
  system local to the LCMAP REST server, capturing standard out, with a
  synthetic (and variable) delay introduced to show asynchronous results."
  (:require [clojure.tools.logging :as log]
            [clj-commons-exec :as exec]
            [lcmap.see.job.tracker :as tracker]
            [lcmap.see.job.tracker.native]))

(defn long-running-func [[job-id sleep-time year]]
  (log/debugf "\n\nRunning job %s (waiting for %s seconds) ...\n"
                     job-id
                     sleep-time)
  @(exec/sh ["sleep" (str sleep-time)])
  (:out @(exec/sh ["cal" year])))

(defn run-model [component job-id default-row result-table seconds year]
  ;; Define some vars for pedagogical clarity
  (let [backend (get-in component [:see :backend :name])
        track-job (tracker/get-tracker-fn backend)
        func #'long-running-func
        args [job-id seconds year]]
    (log/trace "Backend: " backend)
    (log/trace "Tracker function:" track-job)
    (log/trace "Args:" args)
    (track-job component
               job-id
               default-row
               result-table
               [func args])))
