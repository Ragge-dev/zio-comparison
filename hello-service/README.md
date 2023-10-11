# Services and business logic

In order to start simple and look at parts at a time, checkout the branch `service-pattern` using:
```bash
git checkout service-pattern
```

if you're not interested in that you can look at the finished result in here.
In this package we have a look at the Service Pattern in ZIO, and compare that 
to how we usually create services at Kognic. 

The two services are actually very similar. The only real differences being 
the new `Zlayer` syntax of ZIO, and that ZIO has a built in way to lift errors from 
a sequence.
