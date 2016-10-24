require 'test_helper'

class ReviewTest < ActiveSupport::TestCase
  test "sample review" do
    namey = users(:namey)
    testuser = users(:testuser)
    req = requests(:samplecompleted)
    rev = reviews(:fivestar)

    assert_equal namey.id, rev.reviewer.id
    assert_equal testuser.id, rev.reviewee.id
    assert_equal req.id, rev.request.id
    assert_equal 5, rev.rating
  end
end
