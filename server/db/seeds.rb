# This file should contain all the record creation needed to seed the database with its default values.
# The data can then be loaded with the rake db:seed (or created alongside the db with db:setup).
#
# Examples:
#
#   cities = City.create([{ name: 'Chicago' }, { name: 'Copenhagen' }])
#   Mayor.create(name: 'Emanuel', city: cities.first)


chris = User.create(name: 'Chris Konstad',
                    email: 'chriskon149@gmail.com',
                    fbid: '867392256730734',
                    wallet: 100.0)

pramono = User.create(name: 'Pramono Wang',
                      email: 'wang@gmail.com',
                      fbid: '1372155267',
                      wallet: 200.0)

req1 = Request.create(title: "bringing out the trash",
                      user_id: pramono.id,
                      amount: 100.0,
                      lat: 34.0688,
                      longitude: -118.4453,
                      due: DateTime.new(2017,9,1,19))

req2 = Request.create(title: "Cleaning the house",
                      user_id: pramono.id,
                      amount: 50.0,
                      lat: 34.0690,
                      longitude: -118.4455,
                      due: DateTime.new(2016,12,21,19))

req3 = Request.create(title: "Test task",
                       user_id: pramono.id,
                       amount: 42.0,
                       description: 'This is a sample of a task request.',
                       lat: 34.0689,
                       longitude: -118.4452,
                       due: DateTime.new(2016,12,8,19))

req4 = Request.create(title: "Sample Completed Task",
                     user_id: chris.id,
                     amount: 50.0,
                     lat: 34.0690,
                     longitude: -118.4455,
                     due: DateTime.new(2016,12,21,19),
                     description: 'This is a sample task that is completed')

device = Device.create(registration_id: '1', 
                       device_type: 'android',
                       user_id: 1)

Request.handle_action("accept", req4.id, 2)
Request.handle_action("complete", req4.id, 2)

# Review.create(reviewer_id: pramono.id,
#               reviewee_id: chris.id,
#               request_id: req1.id,
#               comment: 'Test comment',
#               rating: 5)

# Review.create(reviewer_id: pramono.id,
#               reviewee_id: chris.id,
#               request_id: req2.id,
#               rating: 4)

# Transaction.create(payer_id: 1, payee_id: 2, amount: 100, request_id: 1)

#post the review
# {"review" : 
#         {"reviewer_id":2,"reviewee_id":1,"request_id":1,"rating":5}
# }

#post the transaction
# {"transaction" : {"payer_id" : 1, "payee_id" : 2, "amount": 100, "request_id": 1}}
