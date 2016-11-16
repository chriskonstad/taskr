# This file should contain all the record creation needed to seed the database with its default values.
# The data can then be loaded with the rake db:seed (or created alongside the db with db:setup).
#
# Examples:
#
#   cities = City.create([{ name: 'Chicago' }, { name: 'Copenhagen' }])
#   Mayor.create(name: 'Emanuel', city: cities.first)


User.create(name: 'Chris Konstad',
            email: 'chriskon149@gmail.com',
            fbid: '867392256730734',
            wallet: 100.0)

User.create(name: 'Pramono Wang',
            email: 'wang@gmail.com',
            fbid: '1372155267',
            wallet: 200.0)

Request.create(title: "bringing out the trash",
        user_id: 2,
        amount: 100.0,
        lat: 34.0688,
        longitude: -118.4453,
        due: DateTime.new(2017,9,1,19)
        )

Request.create(title: "Cleaning the house",
        user_id: 1,
        amount: 50.0,
        lat: 34.0690,
        longitude: -118.4455,
        due: DateTime.new(2016,12,21,19)
        )

Request.create(title: "Test task",
        user_id: 2,
        amount: 42.0,
        description: 'This is a sample of a task request.',
        lat: 34.0689,
        longitude: -118.4452,
        due: DateTime.new(2016,12,8,19)
        )

req = Request.create(title: "Sample Completed Task",
        user_id: 1,
        amount: 50.0,
        lat: 34.0690,
        longitude: -118.4455,
        due: DateTime.new(2016,12,21,19),
        description: 'This is a sample task that is completed'
        )

Request.handle_action("accept", req.id, 2)
Request.handle_action("complete", req.id, 2)

Review.create(reviewer_id: 2, 
            reviewee_id: 1, 
            request_id: 1,
            rating: 5)

Review.create(reviewer_id: 2, 
            reviewee_id: 1, 
            request_id: 2,
            rating: 5)

# Transaction.create(payer_id: 1, payee_id: 2, amount: 100, request_id: 1)

#post the review
# {"review" : 
#         {"reviewer_id":2,"reviewee_id":1,"request_id":1,"rating":5}
# }

#post the transaction
# {"transaction" : {"payer_id" : 1, "payee_id" : 2, "amount": 100, "request_id": 1}}
