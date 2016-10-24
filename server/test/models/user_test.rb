require 'test_helper'

class UserTest < ActiveSupport::TestCase
  test "all components included" do
    assert (User.create(name: "test", email: "test@test.com", wallet: 0).valid?)
    assert (not User.create(name: "test", email: "test@test.com").valid?)
    assert (not User.create(name: "test", wallet: 0).valid?)
    assert (not User.create(email: "test@test.com", wallet: 0).valid?)
  end

  test "unique email" do
    assert User.create(name: "Test", email: "test@test.com", wallet: 0).valid?
    assert (not User.create(name: "Test2", email: "test@test.com", wallet: 0).valid?)
  end

  test "wallet non-negative" do
    assert (not User.create(name: "Test", email: "test@test.com", wallet: -1.0).valid?)
  end
end
