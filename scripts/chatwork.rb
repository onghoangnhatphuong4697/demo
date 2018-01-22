unless ARGV[1].empty?
	require "chatwork"

  # Create message
  ChatWork.api_key = "8f247d4a711fb2326012a8ad47243db3"
  ChatWork::Message.create(room_id: "#{ARGV[0]}", body: "[info](ok2)[hr]Project: #{ARGV[4]} \n\nOwner: #{ARGV[3]} \n\nPlease Check! \nLink : #{ARGV[1]}[hr]#{ARGV[2]}[/info]")
end
