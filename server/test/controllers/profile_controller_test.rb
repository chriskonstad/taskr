require 'test_helper'

class ProfileControllerTest < ActionController::TestCase

  setup do
    @user = users(:namey)
  end

  test "should get show" do
    get :show, id: @user
    assert_response :success
  end


end