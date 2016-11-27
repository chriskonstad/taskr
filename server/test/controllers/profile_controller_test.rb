require 'test_helper'

class ProfileControllerTest < ActionController::TestCase

  setup do
    @user = users(:namey)
  end

  test "should get show" do
    get :show, id: @user
    assert_response :success
  end

   test "should not get show" do 
  	assert_raises(ActionController::UrlGenerationError) do
    	get '/api/v1/profile/show'
  	end
  end

end