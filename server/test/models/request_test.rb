require 'test_helper'

class RequestTest < ActiveSupport::TestCase
  test "check samplecompleted" do
    namey = users(:namey)
    testuser = users(:testuser)
    trans = transactions(:sampletrans)
    req = requests(:samplecompleted)

    assert_equal namey.id, req.user_id
    assert_equal trans.id, req.trans.id
    assert_equal testuser.id, req.trans.payee.id
    assert_equal namey.id, req.trans.payer.id
  end

  test "distance" do
    longA = -77.037852
    latA = 38.898556
    longB = -77.043934
    latB = 38.897147

    assert_equal 0.341, Request.distance(longA, latA, longB, latB).round(3)
  end

  test "check near" do
    completed = requests(:samplecompleted)
    open = requests(:sampleopen)

    # Ensure they take place at the sample place
    # and that one is open and one is closed
    assert_equal completed.long, open.long
    assert_equal completed.lat, open.lat
    assert_not_nil completed.trans
    assert_nil open.trans

    requests = Request.openNear(1.51, 1.51, 5.0)

    assert requests.include? open
    assert_not requests.include? completed
  end
end
