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
end
