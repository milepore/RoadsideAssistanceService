# Overview

Michael Lepore's implementation of "Homework" for job interview.

# Functionality
* Update location of a service provider
* Return the 5 nearest service trucks ordered by ascending distance
* Reserve a service provider for a customer
* Release a service provider from a customer

# Instructions
* Built using OpenJDK 20 and maven 3.9.1 on a macbook
* Run __mvn test__ to execute tests

# Considerations
* There are a lot of things that we'd really want to think about here for example:
  * how many advisors we have would change the data structures
  * along with how frequently the requests come in 
  * I picked a KD tree because it is good at nearest neighbor algorithms - and scales well.
    * but would probably look at something more like a R* Tree if we were updating the locations of advisors more frequently. 
    * would need to probably customize the data structure, I chose not to build from scratch but use one that already exists 
    * most of these types of datastructure aren't great for general purpose use 
    * in the past I've used KDtrees for multidimensionsal data at high frequency and low updates with a built-in
      rebalancing algorithm (doing this in the background in a different thread)
  * If the frequency of requests is low, and/or we have constraints like having to subdivide the entire pool
    of assistants into a regions (for example, only those licensed in each state can service each sate), and each
    those subdivisions is fairly small, it might make more sense to just store these as a set and order the set each
    time we get a request.
* No matter what we do, there isn't a purpose built data structure here, so we want to make sure we wrap what it is and
  can rebuild the implementation without changing things outside.