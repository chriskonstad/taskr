## README

* Ruby version: 2.2.4

* Rails version: 4.2.5

* Database initialization: `rake db:reset`

* How to run the test suite: `rake test`

* How to run server for development: `rails s`

* How to launch interactive console: `rails console`

### TODO
- [ ] Register new user

- [ ] Login as user

- [ ] Logout

- [x] Create request

- [x] Search requests nearby

- [x] Filter requests that are past due

- [x] Edit open request

- [x] Accept open request

  - [x] Ensure status changes

- [x] Reject accepted request

  - [x] Ensure status changes

- [x] Cancel open request

  - [x] Ensure status changes

- [x] Complete request

  - [x] Ensure status changes

  - [ ] Alert the original poster

- [ ] Send payment

  - [x] Ensure status changes

- [ ] Add review of user, one per task

- [x] User profile

  - [x] See reviews of user

  - [x] See posted/completed tasks
  
  - [ ] Add automated tests for more behavior (move code from controllers to pure logically helpers to make it more testable)
