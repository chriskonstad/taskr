class ApiController < ApplicationController

  skip_before_action :verify_authenticity_token

  # Hello world endpoint
  def hello
    render :text => "hello world"
  end

  # Get user profile information
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

  # Create a new user profile
  def createprofile
    user = User.create(name: params[:name], email: params[:email], wallet: params[:wallet])

    if user.save
      # render :text => "creating the profile now"
      render json: user
    else
      render nothing: true, status: :bad_request
    end
  end

  # Show request as JSON given an ID
  def showrequest
    r = Request.where(id: params[:id]).first
    render :json => r.as_json
  end

  # Show all nearby open requests
  def nearby
    # TODO convert to JSON params?
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

  # Create a request with JSON parameters
  def createrequest
    json = params[:request]
    req = Request.create(api_request_creation_params)
    if req.id
      render :json => { "id" => req.id }.to_json
    else
      render nothing: true, status: 500
    end
  end

  # Let the user edit a request, EXCLUDING some values (id, status)
  def editrequest
    id = params[:id]
    json = params[:request]
    req = Request.find_by(id: id)
    if req
      req.update(api_request_edit_params)
      render nothing: true
    else
      render nothing: true, status: 404
    end
  end

  # Show ALL requests
  def products
    requests = Request.all
    render :json => requests.all
  end

  # Show all requests opened by user with ID user_id
  def product
    request = Request.where(user_id: [params[:user_id]])
    render :json => request.as_json
  end

  private
    def api_user_params
      params.permit(:email)
    end

    def api_request_creation_params
      params.require(:request).permit(:title,
                                      :user_id,
                                      :amount,
                                      :lat,
                                      :long,
                                      :due,
                                      :description)
    end

    def api_request_edit_params
      params.require(:request).permit(:title,
                                      :amount,
                                      :lat,
                                      :long,
                                      :due,
                                      :description)
    end
end

