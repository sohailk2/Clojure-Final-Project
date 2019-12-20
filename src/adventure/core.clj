(ns adventure.core
  (:gen-class))
(require '[clojure.string :as str])
(use '[clojure.string :only (split triml)])

(def init-map
	{
	:pen {:desc "The ground is muddy and covered with Hay and Straw. There is a chicken that looks at you inquisitevely 
				and with affection(?). It sits above a pale yellow egg. It looks strangely familiar. It soothes you."
			:title "in the Pen"
			:dir {:south :crack-egg-room
				  :north :garden
				  :east :kitchen
				  :west :preperation-room}
			:contents #{:raw-egg :chicken}
			:suspicion -2
			}

	:crack-egg-room {:desc "The room seems designed for an oddly specific task. You can crack some eggs here. You become uneasy."
			:title "in the crack egg room"
			:dir {:north :pen
				  :south :cutlery-room
				  :east :beat-egg-room}
			:contents #{}
			:suspicion 2
			}

	:garden {:desc "Fully grown vegetables sprawl around you in every direction as far as you can see. Neatly arranged, 
				you see Cilantro on your left, Spring Onions in front of you, and Tomatoes to your right. The green makes you feel calm."
			:title "in the garden"
			:dir {:south :pen}
			:contents #{:cilantro :onion :tomato}
			:suspicion -1
	}

	:dining-room {:desc "You see a giant table strech the legnth of the room. A single throne-like chair sits at the head. 
					 In the center is a pristine glass sculpture of an egg.
					 You look up and see a large dome extend from the middle of the cieling. 
					 Grand paintings depicting people from different eras interacting with chickens spread across the whole cieling. 
					 From the center of the dome descends an ornate chandelier studded with magnificent stones, each one precicesly cut. 
					 The grandiose makes you uncomfortable"
			:title "in the dining room"
			:dir {:west :kitchen}
			:contents #{}
			:suspicion +2
	}

	:kitchen {:desc "You are glad to see a room that looks familiar. However, it seems barren, save a few objects.
				There is a single flame stove, with a non-stick pan (that seems stuck to the stove) resting on top. 
				In the pan is just a little bit of oil. Next to it is a kitchen timer, going up to 3 minutes."
			:title "in the kitchen"
			:dir {:west :pen
				  :east :dining-room}
			:contents #{}
			:suspicion 0
	}

	:cutlery-room {:desc "You open the door to a small room that reaches just above your head. It looks similar to a pantry.
					It is lined with shelves, each seperated from the next by about a foot, 5 in total. 
					Each row has either bowls or forks, in an alternating fashion. 
					You are more amused by the fact that everytime you open the door, 
						you hear a *click* synchronous with the light turning on, and vice-versa when you close it. 
					You attempt to close the door very slowly to find the exact point at which the switch happens." ;pantry
			:title "looking into the cutlery room"
			:dir {:north :crack-egg-room}
			:contents #{:bowl :fork}
			:suspicion -1
	}

	:preperation-room {:desc "The room is uncomfortably empty. The walls are a ghostly white. 
						In the center is a counter, with a sink and a cutting board. 
						It seems like you can wash and cut vegetables in here."
			:title "in the preperation room"
			:dir {:east :pen}
			:contents #{}
			:suspicion 2
	}

	:beat-egg-room {:desc "The room seems designed for an oddly specific task. You can beat some eggs here. You feel anxious."
			:title "in the beat egg room"
			:dir {:west :crack-egg-room}
			:contents #{}
			:suspicion 2
	}
})

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
   :tick 0
   :seen #{}
   :suspicion 0
   :eggs-recieved 0
   })


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

    ;   (if (every? (get-in state [:adventurer :inventory]) (get-in state [:map dest :requires]) ) 
      (do
        (println "\nYou CAN go that way.")
        (assoc-in state [:adventurer :location] dest)
		(assoc-in state [:adventurer :suspicion] (+ (get-in state [:adventurer :suspicion]) (get-in state [:map dest :suspicion]))) ;adventurer->suspicion = adventurer->suspicion + room->suspicion
		;TODO check if suspicion is too high
		(assoc-in state [:adventurer :tick] (+ (get-in state [:adventurer :tick]) 1)) ;adventurer->tick = adventurer->tick + 1
		(assoc-in state [:adventurer :seen] ) ;TODO Update adventurer->seen 
	  	state
	  )
	)

        ; (do (println "You don't have the required objects to go in there.") state) 
))
    ;   ))



(defn status [state]
  (let [location (get-in state [:adventurer :location])
        the-map (:map state)]
    (print (str "\nYou are " (-> the-map location :title) ". "))
    (when-not ((get-in state [:adventurer :seen]) location)
      (print (-> the-map location :desc)))
    (update-in state [:adventurer :seen] #(conj % location))))

(defn describeState [state]

  ; (status state)

  (let [location (get-in state [:adventurer :location])
        the-map (:map state)]
    (print (str "The objects in the scene are" (-> the-map location :title) ". "))
    (when-not ((get-in state [:adventurer :seen]) location)
      (print (-> the-map location :desc)))
    (update-in state [:adventurer :seen] #(conj % location)))
  
  state

)

(defn describeObject [state object]

  ; (status state)

  (do 
    (print "Describe objects")
    state
  )
  

)

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
                    [:describe] describeState
                    [:describe "@"] describeObject 
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