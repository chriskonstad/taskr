class ReviewController < ApplicationController
  skip_before_action :verify_authenticity_token

  def show
    r = Review.where(reviewee_id: params[:id]).first
    render :json => r.as_json
  end


  def create
    rev = Review.create(review_creation_params)
    if rev.id
      render :json => { "id" => rev.id }.to_json
    else
      render nothing: true, status: 500
    end
  end


  private

  def review_creation_params
  	params.require(:review).permit(:reviewer_id,
  									:reviewee_id,
  									:request_id,
  									:rating)
  end


end
