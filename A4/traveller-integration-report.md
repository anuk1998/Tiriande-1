**Task 2 Memo**

To: Our Manager

From: Giselle Briand and Anu Kandasamy

Date: 02/05/2021

Subject: Feedback on Road Network Implementation

The other team implemented our specification very well. They did follow it truthfully, and what they did change was justified and they gave reasoning. For the most part, they only added a few getter methods to access private fields and additional parameters/fields where they were not provided, like in the `query()` method. They did not use Dijkstra’s algorithm, but justified this because Dijkstra’s algorithm traverses on a weighted graph, while in our case, the graph is unweighted.

We were able to successfully integrate the received implementation with our client module. We did have to change our client module slightly to account for differences such as the `id` field, params added to `query()`, and getter methods. Since our spec was originally somewhat vague, there was room for interpretation in those areas. Additionally, our client module did not account for the adjacency matrix in the `TownNetwork` class, since we later thought it was unnecessary in representing the graph. Therefore, we had to add this functionality to our client module. Overall, our efforts were mainly focused on adjusting our client to fit the syntax/exact functionality of the received implementation which was not that difficult since there were only slight differences.

It wasn’t until we were working on Tasks 2 and 3 of Warm-up 3 did we realize that we could have been more detailed in our specification. While we did provide some guidelines and implementation information, we found that it wasn’t nearly enough. As we were working on Task 3 of the previous assignment (implementing a client that followed our own spec), we made some changes to our specification based on what we believed were important changes to make. When we received the artifact of our specification implementation by another team, the other team ended up making similar changes/additions to our specification that we had made. This gave us some assurance that our changes were well-advised. We will definitely be going into our specification and adding in what we want to change so that the documentation is there. Our specification had the right ideas, but they were not discussed or elaborated on to the extent they should have been to make implementation easier.
