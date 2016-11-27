require 'test_helper'

class ReviewControllerTest < ActionController::TestCase
  # test "the truth" do
  #   assert true
  # end

  setup do
    @review = reviews(:fivestar)
  end

  test "should get show" do
    get :show, id: @review
    assert_response :success
    #assert_not_nil assigns(:reviews)
  end

  test "should not get show" do 
  	assert_raises(ActionController::UrlGenerationError) do
    	get '/api/v1/review/show'
  	end
  end



  ##############################################################################################################################
  test "should create review" do
  	post :create, review: {reviewer_id: @review.reviewer_id,  comment: @review.comment ,reviewee_id: @review.reviewee_id, request_id: @review.request_id, rating: @review.rating}
  	assert_response :success, @response.body
  end

  test "should create review without comment " do 
  	post :create, review: {reviewer_id: @review.reviewer_id,  comment: @review.comment ,reviewee_id: @review.reviewee_id, request_id: @review.request_id, rating: @review.rating}
  	assert_response :success, @response.body
  end
  ##############################################################################################################################


  test "should failed create device without reviewee id" do
  	post :create, review: {reviewer_id: @review.reviewer_id, request_id: @review.request_id, rating: @review.rating, comment: @review.comment }
  	assert_response 500
  end

  test "should failed create device without reviewer id" do 
  	post :create, review: {reviewee_id: @review.reviewee_id, request_id: @review.request_id, rating: @review.rating, comment: @review.comment }
  	assert_response 500
  end



  # create_table "reviews", force: :cascade do |t|
  #   t.integer  "reviewer_id"
  #   t.integer  "reviewee_id"
  #   t.integer  "request_id"
  #   t.integer  "rating"
  #   t.datetime "created_at",  null: false
  #   t.datetime "updated_at",  null: false
  #   t.string   "comment"
  # end


end
