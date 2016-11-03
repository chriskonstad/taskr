# This file should contain all the record creation needed to seed the database with its default values.
# The data can then be loaded with the rake db:seed (or created alongside the db with db:setup).
#
# Examples:
#
#   cities = City.create([{ name: 'Chicago' }, { name: 'Copenhagen' }])
#   Mayor.create(name: 'Emanuel', city: cities.first)


User.create(name: 'Chris Konstad',
            email: 'chriskon149@gmail.com',
            wallet: 100.0)

User.create(name: 'Pramono Wang',
            email: 'wang@gmail.com',
            wallet: 200.0)

Request.create(title: "bringing out the trash",
        user_id: 1,
        amount: 100.0,
        lat: 30.0,
        longitude: 50.0,
        due: DateTime.new(2017,9,1,19)
        )

Request.create(title: "Cleaning the house",
        user_id: 1,
        amount: 50.0,
        lat: 10.0,
        longitude: 20.0,
        due: DateTime.new(2016,11,21,19)
        )

Request.create(title: "Test task",
        user_id: 2,
        amount: 42.0,
        lat: 34.0689,
        longitude: -118.4452,
        due: DateTime.new(2016,11,8,19)
        )

Review.create(reviewer_id: 2, 
            reviewee_id: 1, 
            request_id: 1,
            rating: 5)

Review.create(reviewer_id: 2, 
            reviewee_id: 1, 
            request_id: 2,
            rating: 5)

