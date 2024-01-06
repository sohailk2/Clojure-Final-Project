# adventure

## Partners:
* sohailk2
* podar2
* jaredjl2

## Running the game
To start make sure you have lein installed
`brew install leiningen`

Then just run the following in the home directory
`lein run`

## Description of Game:
We decided to make an omolette cooking simulator. 
The purpose of this game is to collect the right ingredients to make an omelette.
However, in doing this you should be careful not to invoke too much suspicion, else you... DIE!

### Features:
* 8 Rooms: 
    * Pen 
    * Crack-Egg-Room 
    * Garden 
    * Dining-Room 
    * Kitchen 
    * Cutlery-Room 
    * Preperation-Room
    * Beat-Egg-Room
* Objects that can be manipulated: 
    * Raw-Egg (take, drop, crack)
    * Cilantro (take, drop, prepare)
    * Onion (take, drop, prepare)
    * Tomato (take, drop, prepare)
    * Bowl (take, drop)
    * Fork (take, drop)
    * Prepared-Vegetables (beat, drop)  - the vegetables can be transformed into this 
    * Cracked-Egg (beat, drop)
    * Beat-Egg (cook, drop) - cracked egg can be transformed into this
    * Omellette (eat, drop)- beat egg can be transformed into this -> eating an omellete wins the game 

* Directions, Supported: (go north, go s, go right, etc...)
    * Cardinal directions and their abbreviations
    * Up, Down, Left, Right

* General Instructions:
    * Move using 'go north, go s, go right, etc...)
    * Look around the room using (describe, look)
    * Pickup/Drop items using 'take, drop'
    * View Inventory using 'inventory, i'
    * Type 'help' to get a list of all actions performable
    * If you really don't want to keep playing this, end game using 'quit'

...


## Known bugs
When in the `Pen` (spawing room), it doesn't mention the `raw-egg` on the ground, but you can still pick it up

## Solution (SPOILER)

This is not the only solution, and may not be the most efficient solution. But it works (in 12 moves). 

```
pickup raw-egg
go north
pickup onion
pickup cilantro
pickup tomato
go south
go west
prepare vegetables
go east
go south
go south
pickup bowl
pickup fork
go north
crack egg
go east
beat egg
go w 
go n
go e
cook egg
go e
eat egg
```



## License

Copyright © 2019 FIXME

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
