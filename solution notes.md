## Note 1. About using serial number as PK

I admit that in reality the serial numbers of drones from different manufacturers may overlap. In this case, we would
use a synthetic key or a composite key (including the manufacturer's name not described in the model). But to simplify
things, let's assume that the serial numbers do not overlap

## Note 2. About NanoId and serial number generation

In a real system, of course, the numbers are not generated, but probably derived from the drone itself and validated
through the manufacturer's API, but I need a way to generate numbers :)

Nano ID is a library for generating random IDs. Likewise UUID, there is a probability of duplicate IDs. However, this
probability is extremely small. I really love that thing!

More than 1 quadrillion years needed, in order to have a 1% probability of at least one collision, if we will generate
1000000000 id per second with using alphabet from this program with id size=36  
<https://zelark.github.io/nano-id-cc>

## Note 3. About NanoId and database side id generation

Of course NanoId can be generated not only in the code, but also on the side of the database. For example, an
implementation of this standard exists for PostgreSQL.

But this is a small application, which is written for demonstration purposes and I use in-mem database, so I will
generate the identifier on the code side.

Also, it also shows that I'm familiar with such cool annotations as @PrePersist/@PreUpdate

## Note 4. About integration tests

Integration tests are here simply because I find it very difficult to repeat these operations by hand every time. They
are not put in order and look terrible. By the way, I have no experience with writing them. Unit-testing, yes, of
course, but I never had a chance to write integration tests.  
So don't pay too much attention to them, that's not what I would like to demonstrate, but what I need for work