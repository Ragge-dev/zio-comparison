# Services and business logic
Because of that I have taken shortcuts with resolving the user.json files you have to run the 
projects from the IDE in order for the programs to correctly resolve the path to the files.

In order to start simple and look at parts at a time, checkout the branch `service-pattern` using:
## Compare Service Pattern
```bash		￼
git checkout service-pattern		
```		

I do recommend starting out in the branch, if you are not solely interested in the ZIO part.
The vanilla Scala code will look very odd without following the train of thought we start
with in the `service-pattern` branch.

In this Hello-Service we have a look at the Service Pattern in ZIO, and compare that
to how we usually create services in vanilla scala.

## Other features we will look at next
- Integration with Futures
- Resource Management
- Retries
