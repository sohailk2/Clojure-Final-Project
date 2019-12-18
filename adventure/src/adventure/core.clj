(ns adventure.core
  (:gen-class))
(require '[clojure.string :as str])
(use '[clojure.string :only (split triml)])

(def init-map
  {
  :pen {:desc ""
           :title "in the Pen. There is a chicken that lays eggs in here"
           :dir {:south :crack-egg-room}
           :contents #{:raw-egg :chicken}
           :requires #{}
           }

  :crack-egg-room {:desc "You can crack somg eggs here."
            :title "in the crack egg room"
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

  :chicken {:desc "This is a chicken. You can pet it to get an egg."
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
        dest ((get-in state [:map location :dir]) dir )]
    (if (nil? dest)
      (do (println "\nYou can't go that way.")
          ; (println dir)
          state)

      (if (every? (get-in state [:adventurer :inventory]) (get-in state [:map dest :requires]) ) 
      (do
        (println "\nYou CAN go that way.")
        (assoc-in state [:adventurer :location] dest))

        (do (println "You don't have the required objects to go in there.") state) 
      ) 
      
      
      
      )
      ))



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

; (defn react [state command]

;   ; (print input-vector)
;   (go state command)

; )

(defn no-understand [state vars] 

  (do 
  
    (print "I don't understand this!")
    state
  )

)

(defn canonicalize
  "Given an input string, strip out whitespaces, lowercase all words, and convert to a vector of keywords."
  [input]
  (def output (split (clojure.string/lower-case input) #"\s+|\.+")) ;regex space or period... lol it actually worked

  ;now need to clean the outputs
  ; (map (fn [input] [str(":" input)]) output)

  ; (map (fn [x] (str ":" x)) output)
  (vec (map (fn [x] (keyword x)) output)))

(defn match [pattern input]
  (loop [pattern pattern
    input input
    vars '()]
    (cond (and (empty? pattern) (empty? input)) (reverse vars)
      (or (empty? pattern) (empty? input)) nil
      (= (first pattern) "@")
      (recur (rest pattern)
      (rest input)
      (cons (first input) vars))
      (= (first pattern) (first input))
      (recur (rest pattern)
      (rest input)
      vars)
      :no-understand nil
    )
  )
)
  
(def initial-env [  [:move "@"] go  
                    [:no-understand] no-understand
                  ])  ;; add your other functions here

(defn react
  "Given a state and a canonicalized input vector, search for a matching phrase and call its corresponding action.
  If there is no match, return the original state and result \"I don't know what you mean.\""
  [state input-vector]
  (loop [idx 0]
    (if (>= idx (count initial-env)) 
    (do
      (print "I really don't know what you're talking about.\n")
      state
    )
    (if-let [vars (match (initial-env idx) input-vector)]
    ; (apply (initial-env (inc idx)) state vars)
    (do
      ; (print vars)
      (apply (initial-env (inc idx)) state vars)
    
    )
    (recur (+ idx 2))
    
    
    ))))

(defn -main
  "Initialize the adventure"
  [& args]
  (loop [local-state {:map init-map :adventurer init-adventurer :items init-items}]
  
  
    (let [pl (status local-state) 
          _  (println "\nWhat do you want to do?")
          command (read-line)]

          (do 
          ; (print ( (get-in local-state [:map (get-in local-state [:adventurer :location]) :dir]) :north) )
          ; (recur (go local-state (canonicalize command)))          
          (recur (react local-state (canonicalize command)))

          )
    )

  )
)





; https://clojuredocs.org/clojure.set/rename-keys for renaming keys
