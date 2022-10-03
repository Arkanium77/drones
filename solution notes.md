# About

## General information

Frankly, I didn't have much time for the task because of my main project. However, the setting is interesting enough
that I don't regret spending time on it, even if the hiring process will be interrupted.
This is more of an outline than a complete system, of course, but in my opinion it meets the requirements.
> Service should allow
> - registering a drone;
> - loading a drone with medication items;
> - checking loaded medication items for a given drone;
> - checking available drones for loading;
> - check drone battery level for a given drone;
>
> Functional Requirements
> - There is no need for UI;
> - Prevent the drone from being loaded with more weight that it can carry;
> - Prevent the drone from being in LOADING state if the battery level is **below 25%**;
> - Introduce a periodic task to check drones battery levels and create history/audit event log for this.

So...

- API for registering a drone - GET /v1/drone
- API for loading a drone with medication items: PUT /v1/drone/{id}, PATCH /v1/drone/{id}
- API for checking loaded medication items: GET /v1/drone/{id}. This api returns all information about drone, and it's
  load
- API for checking available drones for loading: GET /v1/drone/available_for_loading
- API for check drone battery level for a given drone: This information returns from GET /v1/drone/{id}, but also i
  implement additional endpoint GET /v1/drone/{id}/battery, just in case
- There is no need for UI, but I plugged in a swagger, which, to some extent, could be a gui, at least while testing the
  api. For the local profile it is available at http://localhost:9100/swagger   
  ![image](https://i.imgur.com/7YGeeqR.png)
- Prevent the drone from being loaded with more weight that it can carry  [✓]
- Prevent the drone from being in LOADING state if the battery level is **below 25%** [✓]
- Periodic task to check drones battery levels [✓]

## Suggestions for improvement

- Write full-fledged integration and unit tests.
- Rearrange methods between controllers and services to reduce connectivity
- Create a more universal system of additional validation using data from the database. For example, based on functional
  interfaces.
- Add caching (for example via REDIS)
- Use a real relational database (e.g. PostgreSQL)
- Cover all public methods with javadoc

# Notes

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

## Note 5. About periodic task

> Introduce a periodic task to check drones battery levels and create history/audit event log for this.

To be fair: I didn't understand the business purpose of this requirement at all. So I made a periodic task that
literally polls the drones from time to time about their battery level and outputs this information to a separate log
file.
If you imagine that, for example, this information had to be sent to some other database to generate a report instead of
the file appender, we could use, for example, kafka appender. Or collect the message in the desired format in the
method and send it to some HTTP-endpoint.

PS. For testing you can use ```auto-sync-cron: 0/5 * * * * *```