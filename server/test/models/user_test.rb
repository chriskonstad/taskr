require 'test_helper'

class UserTest < ActiveSupport::TestCase
  test "all components included" do
    assert User.create(name: "test", email: "test@test.com", wallet: 0).valid?
    assert_not User.create(name: "test", email: "test@test.com").valid?
    assert_not User.create(name: "test", wallet: 0).valid?
    assert_not User.create(email: "test@test.com", wallet: 0).valid?
  end

  test "unique email" do
    assert User.create(name: "Test", email: "test@test.com", wallet: 0).valid?
    assert_not User.create(name: "Test2", email: "test@test.com", wallet: 0).valid?
  end

  test "wallet non-negative" do
    assert_not User.create(name: "Test", email: "test@test.com", wallet: -1.0).valid?
  end

  test "average rating" do
    user1 = User.create(name: "test1", email: "test1@testemail.com", wallet: 10)
    user2 = User.create(name: "test2", email: "test2@testemail.com", wallet: 10)
    req1 = Request.create(title: "test",
                         user: user1,
                         amount: 100,
                         lat: 100,
                         long: 100,
                         due: "2016-10-24 00:14:55")
    req2 = Request.create(title: "test2",
                         user: user1,
                         amount: 100,
                         lat: 100,
                         long: 100,
                         due: "2016-10-24 00:14:55")
    rev1 = Review.create(reviewer: user1, reviewee: user2, request: req1, rating: 5)
    rev2 = Review.create(reviewer: user1, reviewee: user2, request: req2, rating: 4)

    assert_equal 4.5, user2.avgRating
  end
end
