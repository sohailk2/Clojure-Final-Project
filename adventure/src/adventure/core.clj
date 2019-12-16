(ns adventure.core
  (:gen-class))

(def init-map
  {
  :pen {:desc ""
           :title "in the Pen. There is a chicken that lays eggs in here"
           :dir {:south :crack-egg-room}
           :contents #{:raw-egg :chicken}
           :requires #{}
           }

  :crack-egg-room {:desc "You can crack somg eggs here."
            :title "in the grue pen"
            :dir {:north :pen}
            :contents #{}
            :requires #{:bowl :raw-egg}
            }
  }
)

(def init-items
 {
   :raw-egg {:desc "This is a raw egg.  You probably want to cook it before eating it."
            :name "a raw egg"
            :actions #{:take :crack}
             }

  :chicken {:desc "This is a chicken. You can pet it for some eggs."
            :name "a chicken" 
            :actions #{:pet}
            }
            
            
  })

(def init-adventurer
  {:location :crack-egg-room
   :inventory #{}
   :hp 10
   :lives 3
   :tick 0
   :seen #{}})


; movement code


; (defn go [state dir]
;   (let [location (get-in state [:adventurer :location])
;         dest ((get-in [:map location :dir] state) dir)]
;     (if (nil? dest)
;       (do (println "You can't go that way.")
;           )
;       (assoc-in state [:adventurer :location] dest))
;   )
; )

(defn go [state dir]
  (let [location (get-in state [:adventurer :location])
        dest ((get-in state [:map location :dir]) (keyword dir) )]
    (if (nil? dest)
      (do (println "\nYou can't go that way.")
          (println dir)
          state)

      (do (println "\nYou CAN go that way.")
          (assoc-in state [:adventurer :location] dest))
      )))


(defn status [state]
  (let [location (get-in state [:adventurer :location])
        the-map (:map state)]
    (print (str "You are " (-> the-map location :title) ". "))
    (when-not ((get-in state [:adventurer :seen]) location)
      (print (-> the-map location :desc)))
    (update-in state [:adventurer :seen] #(conj % location))))

; (defn react
;   "Given a state and a canonicalized input vector, search for a matching phrase and call its corresponding action.
;   If there is no match, return the original state and result \"I don't know what you mean.\""
;   [state input-vector]
;   (loop [idx 0]
;     (if (>= idx (count initial-env)) "I really don't know what you're talking␣
;     ,→about."
;     (if-let [vars (match (initial-env idx) input-vector)]
;     (apply (initial-env (inc idx)) state vars)
;     (recur (+ idx 2))))))

(defn react [state command]

  ; (print input-vector)
  (go state command)

)

(defn -main
  "Initialize the adventure"
  [& args]
  (loop [local-state {:map init-map :adventurer init-adventurer :items init-items}]
  
  
    (let [pl (status local-state) 
          _  (println "What do you want to do?")
          command (read-line)]

          (do 
          (print ( (get-in local-state [:map (get-in local-state [:adventurer :location]) :dir]) :north) )
          (recur (react pl command))          

          )
    )

  )
)
