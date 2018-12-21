# WitcherRL
[![IMAGE ALT TEXT](https://i.imgur.com/XmW9EYR.png)](http://www.youtube.com/watch?v=rAGe2vCt_Ts "Video Title")

An alpha build of a roguelike game. To play, download and run WitherRL.jar or execute ApplicationMain.

In WitcherRL you descend into a procedurally generated system of caves using only your skills and what you find to bring a ~~MacGuffin~~ trophy back to the surface.

## Features
#### Procedurally generatd world
Randomly generates a navigable cave system on every new game. The items found in these dungeons and its inhabitants are also random allowing for a variety of situations involving different combinations of foes in different environments. Parts of the cave can be modified at runtime such as by digging into the walls.

#### Variable length turns
Actions are turn based but permit entites to seem faster or slower by allowing for actions to take a variable amount of time. This means a following action will occur later if a preceding action has a high time cost.

#### Creature AI
Creature types have unique AI that allow some to interract with the world and behave in a way identifiable to that creature. For instance, some can pick up and use objects in the world or are affected by certain parts of the world. Others move or attack in particualar patterns. Some of this is accomplished by allowing creatures to schedule actions in advance to be performed over sequential turns.

#### Magic System
The player begins with five signs and the ability to focus their senses that can each be cast when they have enough magic points. Enemies are also capable of using their own magic abilities such as poison and invisibility.
For some abilities not in the main game uncomment PlayScreen.java line 105 `player.equip(factory.newBlueMageSpellbook(0));`

#### Alchemy
The alchemy system allows the player to make and consume potions that provide significant abilities. However, they will be affected by a toxic side effects for roughly the duration of the ability and while toxicity is higher than the player's threshhold they will rapidly lose health. All potoins require three ingredients to craft that must be harvested from discovered plants or earned from slain monsters. The recipes are randomized for each new game.

A list of the current controls and symbols can be accessed during gameplay by pressing 'ESC'. Use the arrow keys to move, moving into another creature will melee attack it.

I began building this project by reading through:
https://trystans.blogspot.com/2016/01/roguelike-tutorial-00-table-of-contents.html.
  
This project uses Trystan's AsciiPanal to display the game tiles:
https://github.com/trystan/AsciiPanel
