Introduction

Asteria 2.0 is a runescape emulator built off of my previous release Asteria. Asteria as a whole has been in development for almost a year now, and I've learned so much since starting the project!
  



What's changed?

Well, to put it simply: everything. All of the problems that the original Asteria faced have been solved with much research, time, and dedication! Almost everything has been rewritten, and I took the time out to write up the major differences between this version and the last.



Design & Complexity: The old overall design didn't really have much thought put into it, so I rewrote a bunch of things and actually planned what I was going to do this time. As a result this version looks completely different from the first one, and is a lot more complex.


Content: This release contains full combat, and due to many requests I stopped development on skills and removed them because people wanted this to have a 'blank project insanity' feel in terms of content.


Networking: The biggest mistake I made with Asteria was removing the original cycle based reactor design that came with Runesource. The networking turned into a complete mess, and actually worsened the performance of Asteria because of all of the queuing and thread safety concerns. I brought back the original reactor design with a twist: connection events are handled on another thread. It's completely safe, and faster.


Task/Events/Workers: Yeah, no more thread pool... simply because carrying out general game logic like that in parallel is not safe at all. A completely new cycle based task system has been designed to take its place.


'Idle' States: If any threads but the main game thread haven't been recieving any activity for a certain amount of time they'll automatically terminate themselves and wait for new work without eating up resources. This allows more resources to be dedicated to the threads that do currently have work to carry out.




How can I help?

Submit pull requests and report issues as needed. Any help is greatly appriciated so thank you in advance if you do choose to help!
