class ApiController < ApplicationController

  skip_before_action :verify_authenticity_token

  def hello
    render :text => "hello world"
  end

  def profile
    id = [params[:id]]
    user = User.find_by!(id: id)
    render :json => user.as_json({:methods => [:avgRating,
                                               :paid,
                                               :earned,
                                               :rated,
                                               :rating,
                                               :request]})
  end

  # def createprofile
  #   user = User.new(api_user_params)

  #   if user.save
  #     render json: user 
  #   else
  #     render nothing: true, status: :bad_request
  #   end
  # end


  def createprofile
    user = User.create(name: params[:name], email: params[:email], wallet: params[:wallet])

    if user.save
      # render :text => "creating the profile now"
      render json: user
    else
      render nothing: true, status: :bad_request
    end
  end

  def nearby
    long = params[:long].to_f
    lat = params[:lat].to_f
    radius_miles = params[:radius].to_f

    if(!params.has_key?(:long) ||
        !params.has_key?(:lat) ||
        !params.has_key?(:radius))
      render nothing: true, status: 404
    else
      requests = Request.openNear(long, lat, radius_miles)
      render :json => requests.as_json
    end
  end

  def products
    requests = Request.all
    render :json => requests.all
  end

  def product
    request = Request.where(user_id: [params[:user_id]])
    render :json => request.as_json
  end

  private
    def api_user_params
      params.permit(:email)
    end
end

