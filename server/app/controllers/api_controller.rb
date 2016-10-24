class ApiController < ApplicationController
  def hello
    render :text => "hello world"
  end

  def profile
    email = [params[:email]]
    all = User.all
    all.each do |u|
      puts u.name
    end
    user = User.find_by(email: email)
    puts user.name
    render :json => user.as_json(methods: :avgRating)
  end

end
